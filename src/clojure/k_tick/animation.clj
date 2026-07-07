(ns clojure.k-tick.animation)

(defn f
  [{:keys [delete-after-stopped?
           looping?
           cnt
           maxcnt]
    :as animation}
   eid
   {:keys [ctx/delta-time]}]
  [[:tx/assoc eid :entity/animation (let [maxcnt (float maxcnt)
                                          newcnt (+ (float cnt) (float delta-time))]
                                      (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                  looping? (min maxcnt (- newcnt maxcnt))
                                                                  :else maxcnt)))]
   (when (and delete-after-stopped?
              (and (not looping?) (>= cnt maxcnt)))
     [:tx/mark-destroyed eid])])
