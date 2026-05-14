(ns com.badlogic.gdx.scenes.scene2d.ui
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [moon.ui.impl.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn- button-class? [actor]
  (some #(= Button %) (supers (class actor))))

(defn button? [actor]
  (or (button-class? actor)
      (and (actor/parent actor)
           (button-class? (actor/parent actor)))))

; FIXME does not work
(defn window-title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (actor/parent actor)]
      (when-let [p (actor/parent p)]
        (and (instance? Window actor)
             (= (window/title-label p) actor))))))

(defn window? [actor]
  (instance? Window actor))
