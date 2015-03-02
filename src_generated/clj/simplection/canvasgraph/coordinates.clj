(ns simplection.canvasgraph.coordinates
  (:require [simplection.canvasgraph.definition :as definition]
            [simplection.canvasgraph.acoordinates :as acoordinates]
            [simplection.canvasgraph.scale :as scale]
                                                                           )
  (      :require                        [simplection.core :as cr])
        (:import [simplection.canvasgraph.acoordinates Cartesian Polar]))

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


(defn normalize-angle
  "Normalizes angles larger than 360 degrees."
  [angle-degrees]
  (rem (+ 360 (rem angle-degrees 360)) 360))

(defn angle-degrees->angle-radians
  "Convert angles in degrees to angles in radians"
  [angle-degrees]
  (/ (* Math/PI (- (normalize-angle angle-degrees) 90)) 180))

(defn polar-point->cartesian-point
  "Converts polar coordinates in cartesian coordinates"
  [center-x center-y radius angle-degrees]
  (let[angle-radians (angle-degrees->angle-radians angle-degrees)]
    {:x (+ center-x (* radius (Math/cos angle-radians)))
     :y (+ center-y (* radius (Math/sin angle-radians)))}))

(defn polar->cartesian
 "Converts a whole collection from polar coordinates to cartesian coordinates"
 [coll-polar-coordinates]
 (let [angle-step (/ 360 (count coll-polar-coordinates))]
   (map #(polar-point->cartesian-point 0 0 %1 (* angle-step %2)) coll-polar-coordinates (range))))

;;;;;;;;;;;; This file autogenerated from src\cljx\simplection\canvasgraph\coordinates.cljx
