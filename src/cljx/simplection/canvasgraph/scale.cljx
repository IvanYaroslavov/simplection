(ns simplection.canvasgraph.scale
  (:require [simplection.range :as ran]
            [simplection.hashmap-ext :as hme]
            [simplection.canvasgraph.series-orderer :as so]
            [simplection.canvasgraph.definition :as definition]))

(def aggregate-keys (hme/select-keys-rest (first so/stacked-table) definition/categories))

(def coordinates-range
  "0-1 range is used and then the Graph is scaled in svg."
  [0 1])

(defprotocol PScale
  (generate-coordinates [this]))

(defrecord Category[table category-keys])

(extend-protocol PScale
  Category
  (generate-coordinates [{table :table category-keys :category-keys}]
    ))

(defrecord Numeric[table aggregate-keys])

(extend-protocol PScale
  Numeric
  (generate-coordinates [{table :table aggregate-keys :aggregate-keys}]
    (ran/table-range-measures table aggregate-keys coordinates-range)))


;; pull definition from table definition; generate-coordinates based on definition
#_(def scaled-table (table-range-measures
                   (table-range-dimensions so/stacked-table category-keys)
                   aggregate-keys))
