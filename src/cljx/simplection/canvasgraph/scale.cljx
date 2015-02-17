(ns simplection.canvasgraph.scale
  (:require [simplection.range :as srange]))

(defprotocol PScale
  (generate-coordinates [this]))

(defrecord Category[coll coordinates-range])

(extend-protocol PScale
  Category
  (generate-coordinates [{categories :coll coordinates-range :coordinates-range}]
    (let [categories-count (count (distinct categories))
          categories-range [1 categories-count]
          offset-coordinates-range (srange/offset-range coordinates-range categories-count)]
      (srange/remap-values categories-range offset-coordinates-range (range 1 (inc categories-count))))))

(defrecord Numeric[coll coordinates-range])

(extend-protocol PScale
  Numeric
  (generate-coordinates [{values :coll coordinates-range :coordinates-range}]
    (let [values-range [(apply min values) (apply max values)]]
      (srange/remap-values values-range coordinates-range values))))
