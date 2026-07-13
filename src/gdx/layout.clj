(ns gdx.layout
  (:require [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn set-fill-parent! [layout fill-parent?]
  (layout/setFillParent layout fill-parent?))

(defn pack [layout]
  (layout/pack layout))
