(ns simplection.canvasgraph.intersection
  (:require [simplection.canvasgraph.definition :as definition]
            [simplection.canvasgraph.acoordinates :as acoordinates]
            [simplection.canvasgraph.scale :as scale])
  (#+clj :require #+cljs :require-macros [simplection.core :as cr]))

(defn cross
  ([] '(()))
  ([xs & more]
    (mapcat #(map (partial cons %)
                  (apply cross more))
            xs)))

(def intersection-fns {:cross cross :interleave interleave})

(defn intersect
  [hm]
  (let [intersect-fn (intersection-fns (hm :intersection))
        data-scaling (definition/get-data-scaling)
        d (hm :data)
        s-1 ((nth data-scaling (first d)) :data)
        s-2 ((nth data-scaling (last d)) :data)]
    (intersect-fn s-1 s-2)))

(defn intersect-data
  [table]
  (reduce #(apply conj % (intersect %2)) [] table))

(def intersection-rules (intersect-data (definition/get-coordinate-systems)))

(defn in?
  "True if a collection contains element."
  [seq element]
  (some #(= element %) seq))

(defn transpose-table [table]
  (let [ks (keys (first table))]
       (into {} (for [k ks] [k (map #(% k) table)]))))

(defn table->series-coordinates
  [table intersection-rules]
  (let [transposed-table (transpose-table table)]
    (into {}
      (for [rule intersection-rules]
         [rule
          (filter #(not (in? % nil))
                  (map vector (transposed-table (first rule)) (transposed-table (last rule))))]))))

(def series-coordinates (table->series-coordinates scale/scaled-table intersection-rules))
