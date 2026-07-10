(ns gdl.scenes.scene2d.utils.layout
  (:require [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn pack [& args]
  (apply layout/pack args))

(defn set-fill-parent! [& args]
  (apply layout/setFillParent args))
