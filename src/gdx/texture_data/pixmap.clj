(ns gdx.texture-data.pixmap
  (:require [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]))

(defn create [pixmap format dispose-pixmap? use-mip-maps?]
  (pixmap-texture-data/new pixmap format dispose-pixmap? use-mip-maps?))
