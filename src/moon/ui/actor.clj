(ns moon.ui.actor
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clj.api.com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn set-opts!
  [actor opts]
  (when-let [touchable (:actor/touchable opts)]
    (actor/set-touchable! actor (case touchable
                                  :touchable/disabled touchable/disabled)))
  (when-let [name (:actor/name opts)]
    (actor/set-name! actor name))
  (when-let [listeners (:actor/listeners opts)]
    (doseq [[listener-k listener-params] listeners]
      (actor/add-listener! actor
                           (case listener-k
                             :listener/change (let [f listener-params]
                                                (change-listener/create f))
                             :listener/text-tooltip (let [[tooltip skin] listener-params]
                                                      (text-tooltip/create tooltip skin))
                             )))))

(defn create
  [opts]
  (actor/create opts))
