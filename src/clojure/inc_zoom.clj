(ns clojure.inc-zoom
  (:require [gdl.graphics.orthographic-camera :as orthographic-camera]
            [clojure.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (orthographic-camera/zoom cam) by))))
