(ns create.require-components
  (:require entity.load
            entity.tick
            entity.render
            entity.create.animation
            entity.create.body
            entity.state.create.active-skill
            entity.state.create.stunned
            entity.state.load
            entity.state.draw-ui-view.player-item-on-cursor
            effect.load
            effect.target.damage
            effect.target-all
            effect.target-entity
            handle-input.player-idle
            handle-input.player-moving
            handle-input.player-item-on-cursor
            tx.load
            editor-widget.load
            editor-widget.one-to-one
            editor-widget.one-to-many
            editor-widget.map
            ))

(defn step [ctx]
  ctx)
