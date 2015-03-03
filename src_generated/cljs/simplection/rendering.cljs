(ns simplection.rendering
  (:require [clojure.string :as string]
            [simplection.canvasgraph.path :as path]))

(defn geometry->svg
  "Convert geometry instructions to svg path"
  [table]
  (for [[k v] table]
    [:path {:d (string/join " " v) :fill "none" :stroke "green" :stroke-width 0.01}]))

(defn generate-graph
  [paths]
  [:svg {:width "100%" :height "100%"}
   [:g {:transform " translate(450, 300) scale(400)"}
    [:g
     paths]]])

(def paths (generate-graph (geometry->svg path/data-paths)))

;;;;;;;;;;;; This file autogenerated from src\cljx\simplection\rendering.cljx