(ns clojure.utils.fit-viewport
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create
  ([width height]
   (proxy [FitViewport ILookup] [width height]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this)))))
  ([width height camera]
   (proxy [FitViewport ILookup] [width height camera]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this))))))
