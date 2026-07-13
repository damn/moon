(ns clojure.gdx.utils.viewport.fit-viewport
  (:require [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn create
  ([world-width world-height]
   (fit-viewport/new world-width world-height))
  ([world-width world-height camera]
   (fit-viewport/new world-width world-height camera)))
