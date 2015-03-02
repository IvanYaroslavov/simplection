(ns simplection.canvasgraph.definition
  (:require [simplection.hashmap-ext :as hme]
            [simplection.canvasgraph.aggregates :as aggs]))

(def default-graph-definition (atom {:extrapolation "to be implemented"
                                     :interpolation "to be implemented"
                                     :aggregate-rules {:DC aggs/category-grouping :DO aggs/series-grouping :DY1 + :DY2 +}
                                     :stack-rules {:type :stack :data [[:DY2 '(:a)] [:DY1 '(:a)] [:DY1 '(:b)] [:DY2 '(:b)]]}
                                     :coordinate-system {:type :cartesian}
                                     :data-scaling [{:type :category :data [[:DC]]}
                                                    {:type :numeric :data [[:DY2 '(:a)] [:DY1 '(:a)] [:DY1 '(:b)] [:DY2 '(:b)]]}]
                                     :cluster-rules "to be implemented"
                                     :intersection :cross
                                     :coordinate-systems [{:type :cartesian :intersection :cross :data [0 1]}]
                                     :data-paths [{:type :straight :data [[[:DC] [:DY2 '(:a)]]
                                                                          [[:DC] [:DY1 '(:a)]]
                                                                          [[:DC] [:DY1 '(:b)]]
                                                                          [[:DC] [:DY2 '(:b)]]]}]
                                     :data-markers "to be implemented"
                                     :data-areas "to be implemented"
                                     :data-mask "to be implemented"}))

(defn get-aggregate-rules
  "Get the aggregate rules for the graph."
  []
  (get-in @default-graph-definition [:aggregate-rules]))

(defn update-aggregate-rules!
  "Update the aggregate rules for the graph."
  [k ag]
  (swap! default-graph-definition update-in [:aggregate-rules] assoc k ag))

(defn remove-aggregate-rules!
  "Remove the aggregate rules for the graph."
  [k ag]
  (swap! default-graph-definition update-in [:aggregate-rules] dissoc k ag))

(defn get-data-scaling
  "Get the data scaling rules for the graph."
  []
  (get-in @default-graph-definition [:data-scaling]))

(defn get-coordinate-systems
  []
  (get-in @default-graph-definition [:coordinate-systems]))

(defn get-coordinate-system
  "Get the coordinate system type for the graph."
  []
  (get-in @default-graph-definition [:coordinate-system]))

(defn get-type
  "Get the type that corresponds to a specific record."
  [hm]
  (hm :type))

(defn get-data
  "Get the data that corresponds to a specific record."
  [hm]
  (hm :data))

(defn get-intersection
  "Get the intersection algorithm for the scaled data."
  [hm]
  (hm :intersection))

(def organize-rules (get-aggregate-rules))
(def categories (hme/keys-by-value organize-rules aggs/category-grouping))
(def series (hme/keys-by-value organize-rules aggs/series-grouping))
(def groupings (concat categories series))
(def aggregates (hme/select-keys-rest organize-rules groupings))
