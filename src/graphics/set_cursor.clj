(ns graphics.set-cursor
  (:require [com.badlogic.gdx.graphics :as graphics]))

(defn f [graphics cursor]
  (graphics/set-cursor! graphics cursor))
