  (ns game.reaction-txs
  (:require [com.badlogic.gdx.audio.sound :as sound]
            [game.info :as info]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.stage :as stage]
            [moon.textures :as textures]
            [moon.ui.inventory-window :as inventory-window]))

(def fn-map
  {
   :tx/sound                    (fn
                                  [{:keys [ctx/audio] :as ctx} sound-name]
                                  (let [sounds audio]
                                    (assert (contains? sounds sound-name) (str sound-name))
                                    (sound/play! (get sounds sound-name)))
                                  ctx)
   :tx/toggle-inventory-visible (fn
                                  [{:keys [ctx/stage] :as ctx}]
                                  (-> stage
                                      (stage/find-actor "moon.ui.windows.inventory")
                                      actor/toggle-visible!)
                                  ctx)
   :tx/show-message             (fn
                                  [{:keys [ctx/stage] :as ctx} message]
                                  (-> stage
                                      (stage/find-actor "player-message")
                                      (actor/set-user-object! (atom {:text message
                                                                     :counter 0})))
                                  ctx)
   :tx/show-modal               (fn
                                  [{:keys [ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   {:keys [title text button-text on-click]}]
                                  (assert (not (stage/find-actor stage "moon.ui.modal-window")))
                                  (stage/add-actor! stage
                                                    (window/create
                                                     {:title title
                                                      :skin skin
                                                      :window/modal? true
                                                      :table/rows [[{:actor (label/create
                                                                             {:text text
                                                                              :skin skin})}]
                                                                   [{:actor (text-button/create
                                                                             {:text button-text
                                                                              :skin skin
                                                                              :actor/listeners {:listener/change (fn [_event _actor]
                                                                                                                   (actor/remove! (stage/find-actor stage "moon.ui.modal-window"))
                                                                                                                   (on-click))}})}]]
                                                      :actor/name "moon.ui.modal-window"
                                                      :actor/position [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                                                       (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))
                                                                       :align/center]}))
                                  ctx)
   :tx/set-item                 (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid cell item]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                                                          :tooltip-text (info/text item ctx)}
                                                                    skin)))
                                  ctx)
   :tx/remove-item              (fn
                                  [{:keys [ctx/stage] :as ctx} eid cell]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/remove-item! cell)))
                                  ctx)
   :tx/add-skill                (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid skill]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        (stage/find-actor "moon.ui.action-bar")
                                        (action-bar/add-skill! {:skill-id (:property/id skill)
                                                                :texture-region (textures/texture-region textures (:entity/image skill))
                                                                :tooltip-text (info/text skill ctx)}
                                                               skin)))
                                  ctx)
   }
  )
