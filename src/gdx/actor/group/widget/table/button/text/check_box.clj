(ns gdx.actor.group.widget.table.button.text.check-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn create [text skin]
  (CheckBox. ^String text ^Skin skin))

(defn checked? [check-box]
  (.isChecked ^CheckBox check-box))

(defn set-checked! [check-box checked?]
  (.setChecked ^CheckBox check-box checked?))
