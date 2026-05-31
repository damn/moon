(ns tx.load
  (:require [game.ctx.do]
            tx.add-text-effect
            tx.set-cooldown
            tx.add-skill
            tx.pickup-item
            tx.remove-item
            tx.spawn-line
            tx.state-enter
            tx.effect
            tx.spawn-item
            tx.spawn-alert
            tx.move-entity
            tx.spawn-effect
            tx.spawn-creature
            tx.state-exit
            tx.audiovisual
            tx.unregister-eid
            tx.spawn-entity
            tx.event
            tx.register-eid
            tx.spawn-projectile
            tx.set-item
            ))

; TODO tx-core/* ?
; just (def txs-fn-map (edn-resource "config/txs-fn-map.edn"))
; possible?

(.bindRoot #'game.ctx.do/txs-fn-map
           {
            :tx/state-exit               tx.state-exit/do!
            :tx/audiovisual              tx.audiovisual/do!
            :tx/assoc                    (fn [_ctx eid k value]
                                           (swap! eid assoc k value)
                                           nil)
            :tx/assoc-in                 (fn [_ctx eid ks value]
                                           (swap! eid assoc-in ks value)
                                           nil)
            :tx/dissoc                   (fn [_ctx eid k]
                                           (swap! eid dissoc k)
                                           nil)
            :tx/update                   (fn [_ctx eid & params]
                                           (apply swap! eid update params)
                                           nil)
            :tx/mark-destroyed           (fn [_ctx eid]
                                           (swap! eid assoc :entity/destroyed? true)
                                           nil)
            :tx/set-cooldown             tx.set-cooldown/do!
            :tx/add-text-effect          tx.add-text-effect/do!
            :tx/add-skill                tx.add-skill/do!
            :tx/set-item                 tx.set-item/do!
            :tx/remove-item              tx.remove-item/do!
            :tx/pickup-item              tx.pickup-item/do!
            :tx/event                    tx.event/do!
            :tx/register-eid             tx.register-eid/do!
            :tx/unregister-eid           tx.unregister-eid/do!
            :tx/state-enter              tx.state-enter/do!
            :tx/effect                   tx.effect/do!
            :tx/spawn-alert              tx.spawn-alert/do!
            :tx/spawn-line               tx.spawn-line/do!
            :tx/move-entity              tx.move-entity/do!
            :tx/spawn-projectile         tx.spawn-projectile/do!
            :tx/spawn-effect             tx.spawn-effect/do!
            :tx/spawn-item               tx.spawn-item/do!
            :tx/spawn-creature           tx.spawn-creature/do!
            :tx/spawn-entity             tx.spawn-entity/do!
            :tx/sound                    (fn [& params] nil)
            :tx/toggle-inventory-visible (fn [& params] nil)
            :tx/show-message             (fn [& params] nil)
            :tx/show-modal               (fn [& params] nil)
            }
           )
