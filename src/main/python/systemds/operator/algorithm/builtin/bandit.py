# -------------------------------------------------------------
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# -------------------------------------------------------------

# Autogenerated By   : src/main/python/generator/generator.py
# Autogenerated From : scripts/builtin/bandit.dml

from typing import Dict, Iterable

from systemds.operator import OperationNode
from systemds.script_building.dag import OutputType
from systemds.utils.consts import VALID_INPUT_TYPES

def bandit(X_train: OperationNode, Y_train: OperationNode, X_val: OperationNode, Y_val: OperationNode, mask: OperationNode, schema: OperationNode, lp: OperationNode, primitives: OperationNode, param: OperationNode, isWeighted: bool, **kwargs: Dict[str, VALID_INPUT_TYPES]) -> OperationNode:
    
    
    X_train._check_matrix_op()
    Y_train._check_matrix_op()
    X_val._check_matrix_op()
    Y_val._check_matrix_op()
    mask._check_matrix_op()
    params_dict = {'X_train':X_train, 'Y_train':Y_train, 'X_val':X_val, 'Y_val':Y_val, 'mask':mask, 'schema':schema, 'lp':lp, 'primitives':primitives, 'param':param, 'isWeighted':isWeighted}
    params_dict.update(kwargs)
    return OperationNode(X_train.sds_context, 'bandit', named_input_nodes=params_dict, output_type=OutputType.LIST, number_of_outputs=3, output_types=[OutputType.FRAME, OutputType.MATRIX, OutputType.MATRIX])


    