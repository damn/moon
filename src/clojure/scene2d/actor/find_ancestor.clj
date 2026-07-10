(ns clojure.scene2d.actor.find-ancestor
  (:require
            [gdl.scenes.scene2d.actor :as actor]))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (actor/get-parent a)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
