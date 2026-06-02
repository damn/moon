(ns gdx.scenes.scene2d.ui
  (:require [clojure.gdx.scene2d.ui.button :as button]
            [clojure.gdx.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.ui.window :as window]))

(defn window? [actor]
  (instance? window/class actor))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn button? [actor]
    (or (button-class? actor)
        (and (.getParent actor)
             (button-class? (.getParent actor))))))

; FIXME does not work
(defn window-title-bar? [actor]
  (when (instance? label/class actor)
    (when-let [p (.getParent actor)]
      (when-let [p (.getParent p)]
        (and (instance? window/class actor)
             (= (window/title-label p) actor))))))
