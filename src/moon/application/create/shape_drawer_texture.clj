(ns moon.application.create.shape-drawer-texture
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture)))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                                      (.setColor 1 1 1 1)
                                                      (.drawPixel 0 0))
                                             texture (Texture. pixmap)]
                                         (.dispose pixmap)
                                         texture)))
