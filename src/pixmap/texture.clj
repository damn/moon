(ns pixmap.texture
  (:require [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f [pixmap]
  (texture/create-from-pixmap pixmap))
