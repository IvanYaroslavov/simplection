(ns simplection.canvasgraph.series-orderer
  (:require [simplection.canvasgraph.aggregator :as aggregator]))

(def table-to-stack (aggregator/organize-table @aggregator/table-to-organize aggregator/organize-rules))

(defn anything->keyword
  "Convert anything to keyword, including numbers and seqs."
  [anything]
  (keyword (str anything)))

(defn keywords
  "Convert a list of values to keywords."
  [coll]
  (map anything->keyword coll))

(defn select-values [hm k-vec]
  (reverse (vals (select-keys hm k-vec))))

(defn generate-key-map
  "Creates the hash-map used for renaming keys."
  [row series aggregates]
  (into {}
        (reduce #(conj %1 [%2 [%2 (keywords (select-values row series))]]) [] aggregates)))

(defn stack-keys
  "Create new vector keys for each aggregate from the existing keys and all dynamic series.
  Example - aggregated values for column :y with dynamic series 'category' and 'subcategory' -> [:y (:category :subcategory)]"
  [table-to-stack series aggregates]
  (for [row table-to-stack]
    (apply dissoc
      (clojure.set/rename-keys row (generate-key-map row series aggregates))
           series)))

(def keys-stacked-table (stack-keys table-to-stack aggregator/series aggregator/aggregates))

(defn dynamic->static-series
  "Convert all the dynamic (row) series to static (column) series."
  [keys-stacked-table categories]
   (for [[k v] (aggregator/group-table keys-stacked-table aggregator/categories)]
    {k (apply merge v)}))

(def static-series (dynamic->static-series keys-stacked-table aggregator/categories))

(defn default-stack-rules
  "By default each series is not stacked."
  [static-series]
  (map vector
    (distinct
      (for [[k v] (apply merge static-series)
            [k-2 v-2] v]
        k-2))))

(def stack-rules (default-stack-rules static-series))

(defn select-values [hm k-vec]
  (reverse (vals (select-keys hm k-vec))))

(defn select-values-empty [map ks]
  (reduce #(conj %1 (map %2)) [] ks))

(def +-nil (fnil + 0 0))

(defn stack-coll [coll]
  (map #(and %1 %2)
    coll
    (reduce #(conj %1 (+-nil (last %1) %2)) [(first coll)] (rest coll))))

(defn stack-multi [hm stack-rules]
  (for [rule stack-rules]
    (stack-coll (select-values-empty hm rule))))

(defn red-concat [coll]
  (reduce concat coll))

(defn stack [hm stack-rules]
  (zipmap
   (red-concat stack-rules)
   (red-concat (stack-multi hm stack-rules))))

(defn stack-table [table stack-rules]
  (for [row table
        [k v] row]
    (merge (stack v stack-rules) k)))
