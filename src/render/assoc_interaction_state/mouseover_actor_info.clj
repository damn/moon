(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [scene2d.ui.window.get-title-label :as get-title-label])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Button Label Window)))

(def ^:private button-class?
  (fn [actor]
    (some #(= Button %) (supers (class actor)))))

(defn- button? [actor]
  (or (button-class? actor)
      (when-let [parent (Actor/.getParent actor)]
        (button-class? parent))))

(defn- window-title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (Actor/.getParent actor)]
      (when-let [p (Actor/.getParent p)]
        (and (instance? Window actor)
             (= (get-title-label/f p) actor))))))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (Actor/.getParent actor)
                            (= "inventory-cell" (Actor/.getName (Actor/.getParent actor)))
                            (Actor/.getUserObject (Actor/.getParent actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar? actor)
     [:mouseover-actor/window-title-bar]

     (button? actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
