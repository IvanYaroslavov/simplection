(ns simplection.canvasgraph.acoordinates)

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
  [center-x center-y [angle-degrees radius]]
  (let[angle-radians (angle-degrees->angle-radians angle-degrees)]
    [(+ center-x (* radius (Math/cos angle-radians)))
     (+ center-y (* radius (Math/sin angle-radians)))]))

(defn polar->cartesian
 "Converts a whole collection from polar coordinates to cartesian coordinates"
 [coll-polar-coordinates]
   (map #(polar-point->cartesian-point 0 0 %) coll-polar-coordinates))

(defn polar-table->cartesian-table
  [table]
  (zipmap (keys table)
    (for [[k v] table]
      (polar->cartesian v))))

(defn polar-grid->cartesian-grid
  [vectors]
  (for [v vectors]
    (let [f (first v)
          r (rest v)]
    (if (= f :path)
      (conj (polar->cartesian r) :path )
      v))))

(defn get-s-lines
  [p-min p-max s-axis-points]
  (for [s s-axis-points]
    [:path [p-min s] [p-max s]]))

(defn get-p-lines
  [s-min s-max p-axis-points]
  (for [p p-axis-points]
    [:path [p s-min] [p s-max]]))

(defn get-circles
  [s-axis-points]
  (for [s s-axis-points]
    [:circle 0 0 s]))

(defprotocol PCoordinateSystem
  (get-coordinates-bounds [this])
  (generate-grid [this axes-points])
  (normalize-table-coordinates [this table])
  (normalize-grid-coordinates [this table]))

(defrecord Cartesian [])
(defrecord Polar [])

(extend-protocol PCoordinateSystem

  Cartesian
  (get-coordinates-bounds
   [this]
   [0 1])

  (generate-grid
   [this axes-points]
   (let [p-axis-points (first axes-points)
         s-axis-points (second axes-points)
         p-min (apply min p-axis-points)
         p-max (apply max p-axis-points)
         s-min (apply min s-axis-points)
         s-max (apply max s-axis-points)
         horizontal-lines (get-s-lines p-min p-max s-axis-points)
         vertical-lines (get-p-lines s-min s-max p-axis-points)]
     (concat [(first horizontal-lines)]
             [(first vertical-lines)]
             (rest horizontal-lines)
             (rest vertical-lines))))

  (normalize-table-coordinates
   [this table]
   table)

  (normalize-grid-coordinates
   [this axes-grid]
   axes-grid)

  Polar
  (get-coordinates-bounds
   [this]
   [0 360])

  (generate-grid
   [this axes-points]
   (let [p-axis-points (first axes-points)
         s-axis-points (second axes-points)
         p-min (apply min p-axis-points)
         p-max (apply max p-axis-points)
         s-min (apply min s-axis-points)
         s-max (apply max s-axis-points)
         s-lines (get-p-lines s-min s-max p-axis-points)
         circles (get-circles s-axis-points)]
     (concat [(first s-lines)]
             [(first circles)]
             (rest s-lines)
             (rest circles))))

  (normalize-table-coordinates
   [this table]
   (polar-table->cartesian-table table))

  (normalize-grid-coordinates
   [this axes-grid]
   (polar-grid->cartesian-grid axes-grid)))
