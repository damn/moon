(ns ctx.entity.destroy.destroy-audiovisual)

(defn f
  [audiovisuals-id eid]
  [[:tx/audiovisual (:body/position (:entity/body @eid)) audiovisuals-id]])
