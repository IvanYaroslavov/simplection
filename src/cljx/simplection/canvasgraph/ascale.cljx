(ns simplection.canvasgraph.ascale
  (:require [simplection.range :as ran]))

(defprotocol PScale
  (generate-coordinates [this table ks cr]))

(defrecord Category[])
(defrecord Numeric[])

(extend-protocol PScale

  Category
  (generate-coordinates [this table ks cr]
    (ran/table-range-dimensions table ks cr))

  Numeric
  (generate-coordinates [this table ks cr]
    (ran/table-range-measures table ks cr)))
