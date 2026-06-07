(ns gdx.scenes.scene2d.ui
  (:require [com.badlogic.gdx.scenes.scene2d.actor.get-parent :refer [get-parent]]
            [com.badlogic.gdx.scenes.scene2d.ui.button :as button]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(defn window? [actor]
  (instance? window/class actor))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn button? [actor]
    (or (button-class? actor)
        (and (get-parent actor)
             (button-class? (get-parent actor))))))

; FIXME does not work
(defn window-title-bar? [actor]
  (when (instance? label/class actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? window/class actor)
             (= (window/title-label p) actor))))))
