(ns moon.txs-impl
  (:require [moon.ctx]))

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    moon.reaction-txs.sound/do!
                 :tx/toggle-inventory-visible moon.reaction-txs.toggle-inventory-visible/do!
                 :tx/show-message             moon.reaction-txs.show-message/do!
                 :tx/show-modal               moon.reaction-txs.show-modal/do!
                 :tx/set-item                 moon.reaction-txs.set-item/do!
                 :tx/remove-item              moon.reaction-txs.remove-item/do!
                 :tx/add-skill                moon.reaction-txs.add-skill/do!
                 }
               requiring-resolve))

(def txs-fn-map
  (update-vals '{
                 :tx/state-exit               moon.tx.state-exit/do!
                 :tx/audiovisual              moon.tx.audiovisual/do!
                 :tx/assoc                    moon.tx.assoc/do!
                 :tx/assoc-in                 moon.tx.assoc-in/do!
                 :tx/dissoc                   moon.tx.dissoc/do!
                 :tx/update                   moon.tx.update/do!
                 :tx/mark-destroyed           moon.tx.mark-destroyed/do!
                 :tx/set-cooldown             moon.tx.set-cooldown/do!
                 :tx/add-text-effect          moon.tx.add-text-effect/do!
                 :tx/add-skill                moon.tx.add-skill/do!
                 :tx/set-item                 moon.tx.set-item/do!
                 :tx/remove-item              moon.tx.remove-item/do!
                 :tx/pickup-item              moon.tx.pickup-item/do!
                 :tx/event                    moon.tx.event/do!
                 :tx/state-enter              moon.tx.state-enter/do!
                 :tx/effect                   moon.tx.effect/do!
                 :tx/spawn-alert              moon.tx.spawn-alert/do!
                 :tx/spawn-line               moon.tx.spawn-line/do!
                 :tx/move-entity              moon.tx.move-entity/do!
                 :tx/spawn-projectile         moon.tx.spawn-projectile/do!
                 :tx/spawn-effect             moon.tx.spawn-effect/do!
                 :tx/spawn-item               moon.tx.spawn-item/do!
                 :tx/spawn-creature           moon.tx.spawn-creature/do!
                 :tx/spawn-entity             moon.tx.spawn-entity/do!
                 :tx/sound                    moon.tx.nothing/do!
                 :tx/toggle-inventory-visible moon.tx.nothing/do!
                 :tx/show-message             moon.tx.nothing/do!
                 :tx/show-modal               moon.tx.nothing/do!
                 }
               requiring-resolve))

(.bindRoot #'moon.ctx/reaction-txs-fn-map reaction-txs-fn-map)
(.bindRoot #'moon.ctx/txs-fn-map txs-fn-map)
