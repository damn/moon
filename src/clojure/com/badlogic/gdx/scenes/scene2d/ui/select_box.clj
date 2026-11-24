(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.select-box
  "A select box (aka a drop-down list) allows a user to choose one of a number of values from a list. When inactive, the selected
  value is displayed. When activated, it shows the list of values that may be selected.
  
  change event is fired when the selectbox selection changes.
  
  The preferred size of the select box is determined by the maximum text bounds of the items and the size of the
  background."
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox Skin)))

(defn create [^Skin skin]
  (SelectBox. skin))

(defn set-items!
  "Set the backing Array that makes up the choices available in the SelectBox"
  [^SelectBox select-box items]
  (.setItems select-box ^"[Ljava.lang.Object;" items))

(defn set-selected!
  "Sets the selection to only the passed item, if it is a possible choice, else selects the first item."
  [^SelectBox select-box item]
  (.setSelected select-box item))

(defn selected [^SelectBox select-box]
  (.getSelected select-box))
