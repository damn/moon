(ns gdl.fit-viewport
  (:require [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn create [& args]
  (apply fit-viewport/new args))
