(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defn create [^String title ^Skin skin]
  (CheckBox. title skin))

(defn set-checked! [^CheckBox check-box checked?]
  (.setChecked check-box checked?))

(defn checked? [^CheckBox check-box]
  (.isChecked check-box))
