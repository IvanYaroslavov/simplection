(ns simplection.canvasgraph.coordinates
  (:require [simplection.canvasgraph.definition :as definition]
            [simplection.canvasgraph.acoordinates :as acoordinates]
                                                                           )
  (      :require                        [simplection.core :as cr])
        (:import [simplection.canvasgraph.acoordinates Cartesian Polar]))

(def coordinates-range
  "0-1 for linear and 0-360 for angular for the first axis."
  [(acoordinates/get-coordinates-bounds ((cr/coordinates-resolver) (definition/get-type (definition/get-coordinate-system))))
   [0 1]])

;;;;;;;;;;;; This file autogenerated from src\cljx\simplection\canvasgraph\coordinates.cljx
