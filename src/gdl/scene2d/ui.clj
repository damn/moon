(ns gdl.scene2d.ui
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn window? [actor]
  (instance? Window actor))

(let [button-class? (fn [actor]
                      (some #(= Button %) (supers (class actor))))]
  (defn button? [^Actor actor]
    (or (button-class? actor)
        (and (.getParent actor)
             (button-class? (.getParent actor))))))

; FIXME does not work
(defn window-title-bar? [^Actor actor]
  (when (instance? Label actor)
    (when-let [p (.getParent actor)]
      (when-let [p (.getParent p)]
        (and (instance? Window actor)
             (= (Window/.getTitleLabel p) actor))))))
