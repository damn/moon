(ns gdx.actor.widget.select-box
  (:require [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]))

(defn create [skin]
  (select-box/new skin))

(defn get-selected [select-box]
  (select-box/getSelected select-box))

(defn set-items! [select-box items]
  (select-box/setItems select-box items))

(defn set-selected! [select-box item]
  (select-box/setSelected select-box item))
