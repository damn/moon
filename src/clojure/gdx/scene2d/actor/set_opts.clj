(ns clojure.gdx.scene2d.actor.set-opts
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

#_(def mappings
  {
   :actor/user-object
   :actor/position
   :actor/visible?
   :actor/touchable
   :actor/name
   :actor/listeners
   }
  )

(defn set-opts! [^Actor actor opts]
  (when-let [user-object (:actor/user-object opts)]
    (.setUserObject actor user-object))

  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (.setPosition actor x y align)
        (.setPosition actor x y))))

  (when (contains? opts :actor/visible?)
    (.setVisible actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (.setTouchable actor touchable))

  (when-let [name (:actor/name opts)]
    (.setName actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (.addListener actor listener)))
  )
