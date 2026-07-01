(ns orthographic-camera.set-zoom
  (:require [clojure.gdx.orthographic-camera.set-zoom :as set-zoom!]
            [clojure.gdx.orthographic-camera.update :as update!]))

(defn set-zoom! [camera amount]
  (set-zoom!/f! camera amount)
  (update!/f! camera))
