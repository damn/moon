(ns gdx.layout
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn set-fill-parent! [layout fill-parent?]
  (.setFillParent ^Layout layout fill-parent?))

(defn pack [layout]
  (.pack ^Layout layout))
