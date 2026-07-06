(ns ctx.info.entity.faction)

(defn f [faction _ctx]
  (str "Faction: " (name faction)))
