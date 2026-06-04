(ns game.ctx.draw-component)

(defn draw-component
  [{:keys [ctx/k->render] :as ctx} entity k v]
  ((k->render k) v entity ctx))
