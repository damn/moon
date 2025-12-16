(ns moon.ui.actor
  (:require [gdl.ui.actor :as actor]
            [moon.ui.stage :as stage]))

(def opts-fn-map
  {:actor/name        actor/set-name!
   :actor/user-object actor/set-user-object!
   :actor/visible?    actor/set-visible!
   :actor/touchable   actor/set-touchable!
   :actor/listener    actor/add-listener!
   :actor/position (fn [a [x y]]
                     (actor/set-position! a x y))
   :actor/center-position (fn [a [x y]]
                            (actor/set-position! a
                                                 (- x (/ (actor/width  a) 2))
                                                 (- y (/ (actor/height a) 2))))})

(defn set-opts! [actor opts]
  (doseq [[k v] opts
          :let [f (get opts-fn-map k)]
          :when f]
    (f actor v))
  actor)

(defmethod stage/build :actor/actor [opts]
  (-> (actor/create opts)
      (set-opts! opts)))
