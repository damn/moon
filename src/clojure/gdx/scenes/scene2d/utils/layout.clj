(ns clojure.gdx.scenes.scene2d.utils.layout
  (:require [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn set-fill-parent! [layout fill-parent?]
  (layout/setFillParent layout fill-parent?))
