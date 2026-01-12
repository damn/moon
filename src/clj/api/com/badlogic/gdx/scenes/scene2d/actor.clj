(ns clj.api.com.badlogic.gdx.scenes.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(def opts-fn-map
  {:actor/name        Actor/.setName
   :actor/user-object Actor/.setUserObject
   :actor/visible?    Actor/.setVisible
   :actor/touchable   Actor/.setTouchable
   :actor/listener    Actor/.addListener
   :actor/position (fn [a [x y]]
                     (Actor/.setPosition a x y))
   :actor/center-position (fn [a [x y]]
                            (Actor/.setPosition a
                                                (- x (/ (Actor/.getWidth  a) 2))
                                                (- y (/ (Actor/.getHeight a) 2))))})

(defn set-opts! [actor opts]
  (doseq [[k v] opts
          :let [f (get opts-fn-map k)]
          :when f]
    (f actor v))
  actor)
