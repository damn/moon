(ns moon.application.create.add-stage-actors.windows
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.graphics.viewport :as viewport]
            [moon.info :as info]
            [moon.state :as state]
            [moon.txs :as txs]
            [moon.ui-actors.windows.info]
            [moon.ui-actors.windows.inventory]))

(defn create [{:keys [ctx/stage] :as ctx}]
  (actor/create
   {:type :ui/group
    :group/actors [(moon.ui-actors.windows.info/create
                    {:title "Entity Info"
                     :actor-name "moon.ui.windows.entity-info"
                     :visible? false
                     :position [(viewport/world-width (stage/viewport stage)) 0]
                     :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                            :as ctx}]
                                        (if-let [eid mouseover-eid]
                                          (info/text (apply dissoc @eid [:entity/skills
                                                                         :entity/faction
                                                                         :active-skill])
                                                     ctx)
                                          ""))
                     :skin (:ctx/skin ctx)})
                   (moon.ui-actors.windows.inventory/create ctx
                                                            (fn clicked-inventory-cell [cell {:keys [ctx/player-eid] :as ctx}]
                                                              (let [entity @player-eid
                                                                    state-k (:state (:entity/fsm entity))]
                                                                (txs/handle! ctx
                                                                             (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                                           player-eid
                                                                                                           cell)))))]
    :actor/name "moon.ui.windows"}))
