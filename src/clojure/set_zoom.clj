(ns clojure.set-zoom
  (:require [gdl.graphics.orthographic-camera :as orthographic-camera]))

(defn set-zoom! [camera amount]
  (orthographic-camera/set-zoom! camera amount)
  (orthographic-camera/update! camera))
