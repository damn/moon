(ns moon.ui.actor
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clj.api.com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn set-opts!
  [actor opts]
  (when-let [touchable (:actor/touchable opts)]
    (actor/set-touchable! actor (case touchable
                                  :touchable/disabled touchable/disabled)))
  (when-let [name (:actor/name opts)]
    (actor/set-name! actor name))
  (when-let [[listener-k listener-fn] (:actor/listener opts)]
    (actor/add-listener! actor
                         (case listener-k
                           :listener/change (change-listener/create listener-fn)))))

(defn create
  [opts]
  (actor/create opts))
