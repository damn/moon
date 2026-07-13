(ns gdx.actor.group.widget.table.button.text
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn create [text skin]
  (text-button/new text skin))
