#_(ns clojure.action-handler-test
  (:require [moon.tx-handler :refer [actions!]]
            [clojure.test :refer :all]))

#_(deftest return-flat-txs
  (let [ctx {:accum (atom [])}
        txs-fn-map {:tx/foobar (fn [_ctx]
                                 [[:tx/bar-baz]
                                  [:tx/bim-bam]])
                    :tx/bar-baz (fn [{:keys [accum]}]
                                  (swap! accum conj 1)
                                  nil)
                    :tx/bim-bam (fn [{:keys [accum]}]
                                  (swap! accum conj 2)
                                  nil)}
        result (actions! txs-fn-map ctx [[:tx/foobar]])]
    (is (= result
           [[:tx/foobar]
            [:tx/bar-baz]
            [:tx/bim-bam]]))
    (is (= @(:accum ctx)
           [1 2]))))
