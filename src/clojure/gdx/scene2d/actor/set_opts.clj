(ns clojure.gdx.scene2d.actor.set-opts
  (:require [clojure.gdx.scene2d.actor.set-name :refer [set-name!]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.actor.set-visible :refer [set-visible!]]
            [clojure.gdx.scene2d.actor.set-touchable :refer [set-touchable!]]
            [clojure.gdx.scene2d.actor.set-position :refer [set-position!]])
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
        (set-position! actor [x y] align)
        (set-position! actor [x y]))))

  (when (contains? opts :actor/visible?)
    (set-visible! actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (set-touchable! actor touchable))

  (when-let [name (:actor/name opts)]
    (set-name! actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener)))
  )
