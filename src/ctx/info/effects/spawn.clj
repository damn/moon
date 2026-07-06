(ns ctx.info.effects.spawn)

(defn f [{:keys [property/pretty-name]} _ctx]
  (str "Spawns a " pretty-name))
