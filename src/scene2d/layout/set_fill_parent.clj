(ns scene2d.layout.set-fill-parent
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn set-fill-parent! [^Layout layout bool]
  (.setFillParent layout bool))
