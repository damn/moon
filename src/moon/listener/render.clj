(ns moon.listener.render)

(def render-fns
  (map requiring-resolve
       '[moon.render.get-stage-ctx/do!
         moon.render.validate/do!
         moon.render.update-mouse/do!
         moon.render.update-mouseover-eid/do!
         moon.render.check-open-debug/do! ; part of inputprocessor/stage  ? also input button click based on state update
         moon.render.assoc-active-entities/do! ; part of 'game-logic' -> before draw everything!??
         moon.render.set-camera-on-player/do!
         ; document graphicc draw layers somewhere (cursor, ui, world-vp (..) , world-map, backgr color )
         ; reverse of that is the interaction state click handle (per render layer)
         moon.render.clear-screen/do!
         moon.render.draw-world-map/do!
         moon.render.draw-on-world-viewport/do!
         moon.render.player-state/do!
         moon.render.assoc-paused/do!
         ; render/when-not-paused [fns]  or update-game?
         ; when-not-paused/ ( == game logic ?)
         moon.render.update-time/do!
         moon.render.update-potential-fields/do!
         moon.render.tick-entities/do!
         ;;
         moon.render.remove-destroyed-entities/do!
         moon.render.window-camera-controls/do!
         moon.render.render-stage/do!
         moon.render.validate/do!]))

(defn do! [ctx]
  (reduce (fn [ctx f]
            (f ctx))
          ctx
          render-fns))
