(ns clojure.gdx.scene2d.actor.set-opts
  (:require [clojure.gdx.scene2d.actor :refer [add-listener!
                                               set-visible!
                                               set-touchable!
                                               set-name!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-opts! [^Actor actor opts]
  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (.setPosition actor x y align)
        (.setPosition actor x y))))

  (when (contains? opts :actor/visible?)
    (set-visible! actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (set-touchable! actor touchable))

  (when-let [name (:actor/name opts)]
    (set-name! actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))
