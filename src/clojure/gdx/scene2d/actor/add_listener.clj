(ns clojure.gdx.scene2d.actor.add-listener
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  ClickListener)))

(defmulti create
  (fn [[listener-k listener-params]]
    listener-k))

(defmethod create
  :listener/text-tooltip
  [[_ [tooltip skin]]]
  (TextTooltip. ^String tooltip ^Skin skin))

(defmethod create
  :listener/change
  [[_ f]]
  (proxy [ChangeListener] []
    (changed [event actor]
      (f event actor))))

(defmethod create
  :listener/click
  [[_ f]]
  (proxy [ClickListener] []
    (clicked [event x y]
      (f event x y))))

(defn add-listener! [actor listener]
  (Actor/.addListener actor (create listener)))
