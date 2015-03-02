(ns simplection.core
  (:require [clojure.string :as string]
            [simplection.canvasgraph.ascale :as scale]
            [simplection.canvasgraph.acoordinates :as coordinates])
  (:import [simplection.canvasgraph.ascale Category Numeric]
           [simplection.canvasgraph.acoordinates Cartesian Polar]))

(defn record-mapper
  [hm record-class]
  (let [class-map (bean record-class)
        full-name (class-map :canonicalName)
        r-name (class-map :simpleName)
        r-ns (string/replace full-name (str "." r-name) "")]
    (conj hm [(keyword (string/lower-case r-name)) `(~(symbol r-ns (str "->" r-name)))])))

(defn record-factory
  [proto]
  (reduce record-mapper {} (extenders proto)))

(defmacro scale-resolver
  []
  (record-factory scale/PScale))

(defmacro coordinates-resolver
  []
  (record-factory coordinates/PCoordinateSystem))
