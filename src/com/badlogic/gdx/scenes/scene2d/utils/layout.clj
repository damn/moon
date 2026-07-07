(ns com.badlogic.gdx.scenes.scene2d.utils.layout
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn pack! [^Layout layout]
  (Layout/.pack layout))

(defn set-fill-parent! [^Layout layout fill-parent?]
  (Layout/.setFillParent layout fill-parent?))
